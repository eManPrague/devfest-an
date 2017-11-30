package lv.rigadevday.android.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.database.*
import cz.eman.android.devfest.lib.app.funtions.user.model.DevFestUser
import durdinapps.rxfirebase2.DataSnapshotMapper
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Function5
import io.reactivex.subjects.PublishSubject
import lv.rigadevday.android.repository.model.partners.Partners
import lv.rigadevday.android.repository.model.schedule.Rating
import lv.rigadevday.android.repository.model.schedule.Schedule
import lv.rigadevday.android.repository.model.schedule.Session
import lv.rigadevday.android.repository.model.schedule.Timeslot
import lv.rigadevday.android.repository.model.speakers.Speaker
import lv.rigadevday.android.utils.auth.AuthStorage
import lv.rigadevday.android.utils.bindSchedulers
import java.util.concurrent.TimeUnit


/**
 * All of the observables provided by repository are non-closable so it is mandatory
 * to unsubscribe any subscription when closing screen to prevent memory leak.
 */
class Repository(val context: Context, val authStorage: AuthStorage, val dataCache: DataCache) {

    val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference.apply { keepSynced(true) }
    }

    val cacheUpdated: PublishSubject<Boolean> by lazy {
        PublishSubject.create<Boolean>().also { sub ->
            Flowable.combineLatest(
                    RxFirebaseDatabase.observeValueEvent(database.child("partners"), DataSnapshotMapper.listOf(Partners::class.java)),
                    RxFirebaseDatabase.observeValueEvent(database.child("speakers"), DataSnapshotMapper.listOf(Speaker::class.java)),
                    RxFirebaseDatabase.observeValueEvent(database.child("schedule"), DataSnapshotMapper.listOf(Schedule::class.java)),
                    RxFirebaseDatabase.observeValueEvent(database.child("sessions"), DataSnapshotMapper.mapOf(Session::class.java)),
                    RxFirebaseDatabase.observeValueEvent(database.child("resources"), DataSnapshotMapper.mapOf(String::class.java)),
                    Function5 { _: List<Partners>, _: List<Speaker>, _: List<Schedule>, _: Map<String, Session>, _: Map<String, String> -> }
            )
                    .debounce(1, TimeUnit.SECONDS)
                    .skip(1)
                    .flatMapSingle { updateCache() }
                    .subscribe {
                        sub.onNext(true)
//                        context.showMessage(R.string.data_updated)
                    }
        }
    }

    private fun getCache(predicate: () -> Boolean): Single<DataCache> =
            if (predicate()) Single.just(dataCache)
            else updateCache()


    // Basic requests
    private fun updateCache(): Single<DataCache> {
        RxFirebaseDatabase.observeValueEvent(database.child("speakers"), DataSnapshotMapper.listOf(Speaker::class.java))
                .map { dataCache.updateSpeakers(it) }
                .subscribe({
                    RxFirebaseDatabase.observeValueEvent(database.child("resources"), DataSnapshotMapper.mapOf(String::class.java))
                            .map { dataCache.updateResources(it) }.subscribe({
                        RxFirebaseDatabase.observeValueEvent(database.child("sessions"), DataSnapshotMapper.mapOf(Session::class.java))
                                .map { dataCache.updateSessions(it) }.subscribe({
                            RxFirebaseDatabase.observeValueEvent(database.child("partners"), DataSnapshotMapper.listOf(Partners::class.java))
                                    .map { dataCache.updatePartners(it) }.subscribe({
                                RxFirebaseDatabase.observeValueEvent(database.child("schedule"), DataSnapshotMapper.listOf(Schedule::class.java))
                                        .map { dataCache.updateSchedules(it) }.subscribe({}
                                )
                            })
                        })
                    })
                })


        return Single.just(DataCache())
    }

    // Basic requests
//    fun updateCache(): Single<DataCache> {RxFirebaseDatabase
//            .observeSingleValueEvent(database, Root::class.java)
//            .map { dataCache.update(it) }
//            .toSingle()

    fun speakers(): Flowable<Speaker> = getCache { dataCache.speakers.isNotEmpty() }
            .flattenAsFlowable { it.speakers.values }
            .bindSchedulers()

    fun speaker(id: Int): Single<Speaker> = getCache { dataCache.speakers.isNotEmpty() }
            .map { it.speakers.getValue(id) }
            .bindSchedulers()

    fun schedule(): Flowable<Schedule> = getCache { dataCache.schedule.isNotEmpty() }
            .flattenAsFlowable { it.schedule.values }
            .bindSchedulers()

    fun partners(): Flowable<Partners> = getCache { dataCache.partners.isNotEmpty() }
            .flattenAsFlowable { it.partners }
            .bindSchedulers()

    fun sessions(): Flowable<Session> = getCache { dataCache.sessions.isNotEmpty() }
            .flattenAsFlowable { it.sessions.values }
            .bindSchedulers()

    fun session(id: Int): Single<Session> = getCache { dataCache.sessions.isNotEmpty() }
            .map { it.sessions.getValue(id) }
            .bindSchedulers()

    fun scheduleDayTimeslots(dateCode: String): Flowable<Timeslot> = getCache { dataCache.schedule.isNotEmpty() }
            .flattenAsFlowable { it.schedule.getValue(dateCode).timeslots }
            .bindSchedulers()

    fun devFestAreaKML(): Single<String> = RxFirebaseDatabase
            .observeSingleValueEvent(database.child("maplayer"), String::class.java)
            .toSingle()

    // Read-Write stuff
    // Ratings
    fun sessionRating() = database.child("userFeedbacks").child(authStorage.uId)

    fun rating(sessionId: Int): Single<Rating> = if (authStorage.hasLogin) {
        RxFirebaseDatabase.observeSingleValueEvent(
                sessionRating().child(sessionId.toString()),
                Rating::class.java
        ).toSingle(Rating())
    } else Single.just(Rating())

    fun saveRating(sessionId: Int, rating: Rating) {
        if (authStorage.hasLogin) {
            sessionRating().child(sessionId.toString()).setValue(rating)
        }
    }

    // Bookmarks
    private fun bookmarkedSessions() = database.child("userSessions").child(authStorage.uId)

    fun bookmarkedIds(): Single<List<String>> = if (authStorage.hasLogin) {
        RxFirebaseDatabase.observeSingleValueEvent(
                bookmarkedSessions(),
                DataSnapshotMapper.mapOf(Boolean::class.java)
        ).map { it.keys.toList() }.toSingle(emptyList())
    } else {
        Single.just(emptyList())
    }

    fun isSessionBookmarked(sessionId: Int): Single<Boolean> = bookmarkedIds()
            .map { it.contains(sessionId.toString()) }

    fun bookmarkSession(sessionId: Int) {
        if (authStorage.hasLogin) {
            bookmarkedSessions().child(sessionId.toString()).setValue(true)
        }
    }

    fun removeBookmark(sessionId: Int) {
        if (authStorage.hasLogin) {
            bookmarkedSessions().child(sessionId.toString()).removeValue()
        }
    }

    fun saveUser(name: String, email: String, photoUrl: Uri?, id: String) {
        users().child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    users().child(id).setValue(DevFestUser(name = name, email = email,
                            photoUrl = photoUrl?.toString() ?: "null"))

                    cdhProgress().child(id).setValue("dummy progress")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Do nothing here
            }
        })
    }

    private fun users() = database.child("users")

    private fun cdhProgress() = database.child("cdhProgress")

    fun existsUser(uid: String): Boolean {
        return users().child(uid) != null
    }
}
