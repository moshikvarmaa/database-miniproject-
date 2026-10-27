-- Cognition project query set. Run each query individually in SQL Developer.
-- Q1 Simple SELECT: active learners
SELECT learner_id, participant_code, display_name, created_at
FROM learner WHERE active_flag = 'Y' ORDER BY learner_id;

-- Q2 JOIN: sessions with learner and topic
SELECT s.session_id, l.participant_code, t.topic_name, s.started_at, s.status
FROM learning_session s
JOIN learner l ON l.learner_id = s.learner_id
JOIN topic t ON t.topic_id = s.topic_id
ORDER BY s.session_id;

-- Q3 Aggregate/function: sessions per learner (includes learners with zero sessions)
SELECT l.participant_code, COUNT(s.session_id) AS session_count
FROM learner l LEFT JOIN learning_session s ON s.learner_id = l.learner_id
GROUP BY l.participant_code
ORDER BY session_count DESC;

-- Q4 Nested query: learners with more than one CONFUSION interaction
SELECT l.learner_id, l.participant_code
FROM learner l
WHERE l.learner_id IN (
    SELECT s.learner_id
    FROM learning_session s
    JOIN interaction i ON i.session_id = s.session_id
    WHERE i.event_type = 'CONFUSION'
    GROUP BY s.learner_id
    HAVING COUNT(*) > 1
);

-- Q5 SQL function + GROUP BY: average signal confidence by signal type
SELECT signal_type, ROUND(AVG(confidence), 3) AS avg_confidence,
       COUNT(*) AS signal_count
FROM signal
GROUP BY signal_type
ORDER BY avg_confidence DESC;

-- Q6 Concepts needing practice
SELECT l.participant_code, cp.concept_name, cp.mastery_score
FROM concept_progress cp
JOIN learner l ON l.learner_id = cp.learner_id
WHERE cp.status = 'NEEDS_PRACTICE'
ORDER BY cp.mastery_score;

-- Q7 Complex report: learner, topic, interaction counts and confusion count
SELECT l.participant_code, t.topic_name,
       COUNT(i.interaction_id) AS total_interactions,
       SUM(CASE WHEN i.event_type = 'CONFUSION' THEN 1 ELSE 0 END) AS confusion_count
FROM learner l
JOIN learning_session s ON s.learner_id = l.learner_id
JOIN topic t ON t.topic_id = s.topic_id
LEFT JOIN interaction i ON i.session_id = s.session_id
GROUP BY l.participant_code, t.topic_name
ORDER BY confusion_count DESC;

-- Q8 Date/string functions (Oracle)
SELECT interaction_id, TO_CHAR(event_time, 'YYYY-MM-DD HH24:MI') AS event_time,
       UPPER(speaker) AS speaker, event_type
FROM interaction ORDER BY event_time DESC;

-- Q9 UPDATE example
UPDATE concept_progress
SET status = 'IMPROVING', mastery_score = 78, updated_at = SYSTIMESTAMP
WHERE learner_id = (SELECT learner_id FROM learner WHERE participant_code = 'C001')
  AND concept_name = 'Foreign keys';
-- COMMIT only if you want to retain this demo update; otherwise ROLLBACK.

-- Q10 INSERT example (test record)
INSERT INTO learner (participant_code, display_name) VALUES ('CTEST', 'Temporary Test Learner');
-- DELETE example: remove only this test record
DELETE FROM learner WHERE participant_code = 'CTEST';
-- TCL example
SAVEPOINT before_demo_change;
UPDATE learner SET active_flag = 'N' WHERE participant_code = 'C003';
ROLLBACK TO before_demo_change;
ROLLBACK;
