--liquibase formatted sql

--changeset fmi:010-create-session-speakers-table
CREATE TABLE session_speakers (
    session_id BIGINT NOT NULL,
    speaker_id BIGINT NOT NULL,

    PRIMARY KEY (session_id, speaker_id),

    CONSTRAINT fk_session
        FOREIGN KEY (session_id)
        REFERENCES sessions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_speaker
        FOREIGN KEY (speaker_id)
        REFERENCES speakers(id)
        ON DELETE CASCADE
);