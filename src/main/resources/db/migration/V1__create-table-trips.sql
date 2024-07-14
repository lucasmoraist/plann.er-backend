CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE trips (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    destination VARCHAR(255) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    is_confirmed BOOLEAN NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    owner_email VARCHAR(255) NOT NULL
);
