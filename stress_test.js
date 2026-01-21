import http from 'k6/http';
import { check } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
    // simulate 50 concurrent users trying to debit at the exact same time
    vus: 50,
    // total of 50 requests (1 per user)
    iterations: 50,
    thresholds: {
        // fail the test if any request returns an error (non-2xx/3xx)
        http_req_failed: ['rate<0.01'],
        // optional: assert that 95% of requests are faster than 2 seconds (locking might slow things down)
        http_req_duration: ['p(95)<2000'],
    },
};

// target account id from our database
const account_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11';
const base_url = 'http://localhost:8080';

export default function () {
    const url = `${base_url}/transactions`;

    const payload = JSON.stringify({
        accountId: account_id,
        amount: 1.00,
        type: 'DEBIT',
    });

    // generate a unique idempotency key for each request using uuidv4
    // this is crucial: we want to bypass the redis cache check and force the database lock to activate
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Idempotency-Key': `k6-stress-${uuidv4()}`,
        },
    };

    const res = http.post(url, payload, params);

    // expect 201 created for successful transaction
    check(res, {
        'status is 201': (r) => r.status === 201,
    });
}