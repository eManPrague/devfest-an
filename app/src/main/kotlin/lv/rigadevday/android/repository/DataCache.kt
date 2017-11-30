package lv.rigadevday.android.repository

import lv.rigadevday.android.repository.model.Root
import lv.rigadevday.android.repository.model.partners.Partners
import lv.rigadevday.android.repository.model.schedule.Schedule
import lv.rigadevday.android.repository.model.schedule.Session
import lv.rigadevday.android.repository.model.speakers.Speaker
import java.util.LinkedHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataCache @Inject constructor() {

    var partners: List<Partners> = emptyList()
    var resources: Map<String, String> = emptyMap()

    var speakers: Map<Int, Speaker> = emptyMap()
    var sessions: Map<Int, Session> = emptyMap()
    var schedule: Map<String, Schedule> = emptyMap()

    fun updatePartners(partners: List<Partners>) {
        this.partners = partners

        // enrich partner groups with actual titles
        partners.forEach { it.actualTitle = resources[it.title] ?: it.title }
    }

    fun updateSpeakers(speakers: List<Speaker>) {
        this.speakers = speakers.filterNotNull().associate { it.id to it }
    }

    fun updateSchedules(schedules: List<Schedule>) {
        this.schedule = schedules.filterNotNull().associate { it.date to it }

        schedule.forEach { (date, day) ->
            day.timeslots.forEach { timeslot ->
                // enrich schedule with sessions
                timeslot.sessionObjects = timeslot.sessionIds.map { sessions.getValue(it) }

                // enrich session with info about timeslot
                timeslot.sessionObjects.forEachIndexed { index, session ->
                    session.time = timeslot.startTime
                    session.date = date
                    session.room = if (session.track == null) day.tracks[index].title else session.track.title
                }
            }
        }
    }

    fun update(newData: Root): DataCache {
        partners = newData.partners.filterNotNull()
        resources = newData.resources.filterNot { it.key.isNullOrEmpty() || it.value.isNullOrEmpty() }

        speakers = newData.speakers.filterNotNull().associate { it.id to it }
        sessions = newData.sessions.filterNot { it.key.isNullOrEmpty() }.mapKeys { (key, _) -> key.toInt() }
        schedule = newData.schedule.filterNotNull().associate { it.date to it }

        sessions.forEach { (_, session) ->
            // enrich sessions with speakers
            session.speakerObjects = session.speakers
                .map { speakers.getValue(it) }
                .toMutableList()
        }

        schedule.forEach { (date, day) ->
            day.timeslots.forEach { timeslot ->
                // enrich schedule with sessions
                timeslot.sessionObjects = timeslot.sessionIds.map { sessions.getValue(it) }

                // enrich session with info about timeslot
                timeslot.sessionObjects.forEachIndexed { index, session ->
                    session.time = timeslot.startTime
                    session.date = date
                    session.room = if (session.track == null) day.tracks[index].title else session.track.title
                }
            }
        }

        // enrich partner groups with actual titles
        partners.forEach { it.actualTitle = resources[it.title] ?: it.title }
        return this
    }

    fun updateSessions(sessionsParam: Map<String, Session>) {
      this.sessions = sessionsParam.filterNot { it.key.isNullOrEmpty() }.mapKeys { (key, _) -> key.toInt() }

        this.sessions.forEach { (_, session) ->
            // enrich sessions with speakers
            session.speakerObjects = session.speakers
                    .map { speakers.getValue(it) }
                    .toMutableList()
        }
    }

    fun updateResources(resources: LinkedHashMap<String, String>) {
        this.resources = resources.filterNot { it.key.isNullOrEmpty() || it.value.isNullOrEmpty() }
    }

}
