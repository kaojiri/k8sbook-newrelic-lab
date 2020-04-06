CREATE TABLE region
(
  region_id          SERIAL PRIMARY KEY,
  region_name        VARCHAR(100) NOT NULL,
  creation_timestamp TIMESTAMP    NOT NULL
);

CREATE TABLE location
(
  location_id   BIGSERIAL PRIMARY KEY,
  location_name VARCHAR(200) NOT NULL,
  region_id     BIGINT       NOT NULL,
  note          TEXT,
  FOREIGN KEY (region_id) REFERENCES region (region_id)
);
