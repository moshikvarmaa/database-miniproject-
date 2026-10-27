# Cognition Learner Intelligence Database System — Report Outline

## 1. Requirement Analysis
Cognition's database component maintains structured records of learners, topics, learning sessions, learner/tutor interactions, interaction signals, concept progress, and learner-state transitions. The system is intended to support querying learning activity across sessions and demonstrate core relational database design and Oracle SQL operations.

### Scope
The mini-project includes relational schema design, keys and integrity constraints, synthetic data population, SQL queries, and a Java JDBC console application. It does not claim to implement a validated learner-state prediction model.

### Business Rules
1. A learner has one unique participant code.
2. Each learning session belongs to one learner and one topic.
3. A learner and topic may each be associated with many sessions.
4. A session may contain multiple interactions and state transitions.
5. Each signal belongs to one interaction; an interaction may have multiple signals.
6. Concept progress is recorded for a learner/topic/concept combination.
7. Mastery score must be between 0 and 100.
8. Session status and signal/state labels are restricted to allowed values.

## 2. Entity and Relationship Summary
- LEARNER(learner_id, participant_code, display_name, created_at, active_flag)
- TOPIC(topic_id, topic_name, description)
- LEARNING_SESSION(session_id, learner_id, topic_id, started_at, ended_at, status)
- INTERACTION(interaction_id, session_id, event_time, speaker, message_text, event_type, concept_name)
- SIGNAL(signal_id, interaction_id, signal_type, confidence, review_status)
- STATE_TRANSITION(transition_id, session_id, from_state, to_state, event_time, reason)
- CONCEPT_PROGRESS(progress_id, learner_id, topic_id, concept_name, status, mastery_score, updated_at)

## 3. ER-to-Relational Mapping
- Strong entities become relations with their primary keys.
- 1:N relationships are mapped by placing the primary key of the 1-side as a foreign key in the N-side.
- LEARNER to LEARNING_SESSION: learner_id FK in LEARNING_SESSION.
- TOPIC to LEARNING_SESSION: topic_id FK in LEARNING_SESSION.
- LEARNING_SESSION to INTERACTION: session_id FK in INTERACTION.
- INTERACTION to SIGNAL: interaction_id FK in SIGNAL.
- LEARNING_SESSION to STATE_TRANSITION: session_id FK in STATE_TRANSITION.
- LEARNER and TOPIC to CONCEPT_PROGRESS: learner_id and topic_id FKs in CONCEPT_PROGRESS.

## 4. Implementation and Evaluation Evidence
Attach actual output screenshots for:
- table creation and constraints
- sample data
- at least five SELECT queries (simple, complex/join, nested, aggregate/functions)
- INSERT, UPDATE, DELETE
- COMMIT, SAVEPOINT, ROLLBACK
- front-end Insert, Update, Delete, Search, Display operations

## 5. Limitations
The project stores interaction event labels and learner progress records; labels in demo data are synthetic. The database alone does not prove learning improvement or psychological state. Any real learner study would require appropriate consent and institutional guidance.
