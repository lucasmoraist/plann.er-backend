CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE activities(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    occurs_at TIMESTAMP NOT NULL,
    trip_id UUID NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trips(id)
)