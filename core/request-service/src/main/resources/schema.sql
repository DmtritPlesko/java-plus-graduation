CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE,
    event_id BIGINT,
    requester_id BIGINT,
    status VARCHAR(100),
    UNIQUE(event_id, requester_id)
);