CREATE TABLE links(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    trip_id UUID NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trips(id)
);