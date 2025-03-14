CREATE TABLE itineraries (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_by VARCHAR(100) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    modified_date TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

CREATE TABLE itinerary_stops (
    id BIGSERIAL PRIMARY KEY,
    itinerary_id BIGINT NOT NULL REFERENCES itineraries(id) ON DELETE CASCADE,
    location VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_date TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    modified_date TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);