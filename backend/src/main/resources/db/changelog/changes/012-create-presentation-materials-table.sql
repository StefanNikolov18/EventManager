--liquibase formatted sql

--changeset fmi:012-create-presentation-materials-table
CREATE TABLE presentation_materials (
    id BIGSERIAL PRIMARY KEY,

    speaker_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,

    file_url VARCHAR(500) NOT NULL,
    file_type VARCHAR(100),
    upload_time TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_pm_speaker
        FOREIGN KEY (speaker_id)
        REFERENCES speakers(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pm_session
        FOREIGN KEY (session_id)
        REFERENCES sessions(id)
        ON DELETE CASCADE
);