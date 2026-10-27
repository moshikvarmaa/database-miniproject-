-- Sample/demo records only; these are synthetic, not real student data.
INSERT INTO learner (participant_code, display_name) VALUES ('C001', 'Demo Learner One');
INSERT INTO learner (participant_code, display_name) VALUES ('C002', 'Demo Learner Two');
INSERT INTO learner (participant_code, display_name) VALUES ('C003', 'Demo Learner Three');

INSERT INTO topic (topic_name, description) VALUES ('Database Systems', 'Relational design and SQL fundamentals');
INSERT INTO topic (topic_name, description) VALUES ('Computer Networks', 'Networking concepts and protocols');

INSERT INTO learning_session (learner_id, topic_id, status)
SELECT l.learner_id, t.topic_id, 'COMPLETED'
FROM learner l CROSS JOIN topic t
WHERE l.participant_code = 'C001' AND t.topic_name = 'Database Systems';

INSERT INTO learning_session (learner_id, topic_id, status)
SELECT l.learner_id, t.topic_id, 'OPEN'
FROM learner l CROSS JOIN topic t
WHERE l.participant_code = 'C002' AND t.topic_name = 'Computer Networks';

INSERT INTO interaction (session_id, speaker, message_text, event_type, concept_name)
SELECT session_id, 'LEARNER', 'I am confused about primary keys.', 'CONFUSION', 'Primary keys'
FROM learning_session
WHERE learner_id = (SELECT learner_id FROM learner WHERE participant_code = 'C001')
FETCH FIRST 1 ROW ONLY;

INSERT INTO interaction (session_id, speaker, message_text, event_type, concept_name)
SELECT session_id, 'LEARNER', 'Can you give another example of a foreign key?', 'EXAMPLE_SEEKING', 'Foreign keys'
FROM learning_session
WHERE learner_id = (SELECT learner_id FROM learner WHERE participant_code = 'C001')
FETCH FIRST 1 ROW ONLY;

INSERT INTO interaction (session_id, speaker, message_text, event_type, concept_name)
SELECT session_id, 'LEARNER', 'I still need clarification on primary keys.', 'CONFUSION', 'Primary keys'
FROM learning_session
WHERE learner_id = (SELECT learner_id FROM learner WHERE participant_code = 'C001')
FETCH FIRST 1 ROW ONLY;

INSERT INTO interaction (session_id, speaker, message_text, event_type, concept_name)
SELECT session_id, 'TUTOR', 'A foreign key refers to a key in another table.', 'GENERAL', 'Foreign keys'
FROM learning_session
WHERE learner_id = (SELECT learner_id FROM learner WHERE participant_code = 'C001')
FETCH FIRST 1 ROW ONLY;

INSERT INTO signal (interaction_id, signal_type, confidence, review_status)
SELECT interaction_id, 'CONFUSION', 0.920, 'UNREVIEWED'
FROM interaction WHERE event_type = 'CONFUSION';

INSERT INTO signal (interaction_id, signal_type, confidence, review_status)
SELECT interaction_id, 'EXAMPLE_SEEKING', 0.870, 'ACCEPTED'
FROM interaction WHERE event_type = 'EXAMPLE_SEEKING';

INSERT INTO state_transition (session_id, from_state, to_state, reason)
SELECT session_id, 'START', 'CLARIFICATION_NEEDED', 'Demo transition after confusion signal'
FROM learning_session
WHERE learner_id = (SELECT learner_id FROM learner WHERE participant_code = 'C001')
FETCH FIRST 1 ROW ONLY;

INSERT INTO concept_progress (learner_id, topic_id, concept_name, status, mastery_score)
SELECT l.learner_id, t.topic_id, 'Primary keys', 'NEEDS_PRACTICE', 45
FROM learner l CROSS JOIN topic t
WHERE l.participant_code = 'C001' AND t.topic_name = 'Database Systems';

INSERT INTO concept_progress (learner_id, topic_id, concept_name, status, mastery_score)
SELECT l.learner_id, t.topic_id, 'Foreign keys', 'IMPROVING', 72
FROM learner l CROSS JOIN topic t
WHERE l.participant_code = 'C001' AND t.topic_name = 'Database Systems';

COMMIT;
