CREATE TABLE account (
                         id UUID PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         balance DECIMAL(19, 0) NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transaction (
                                    id UUID PRIMARY KEY,
                                    amount DECIMAL(19, 0) NOT NULL,
                                    description VARCHAR(255),
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE entry (
                              id UUID PRIMARY KEY,
                              amount DECIMAL(19, 0) NOT NULL,
                              type VARCHAR(20) NOT NULL, -- DEBIT / CREDIT
                              account_id UUID REFERENCES account(id),
                              transaction_id UUID REFERENCES transaction(id),
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);