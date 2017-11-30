package lv.rigadevday.android.utils

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import lv.rigadevday.android.repository.Repository


fun <T> Iterable<T>.asFlowable(): Flowable<T> = Flowable.fromIterable<T>(this)

fun <T> T.asFlowable(): Flowable<T> = Flowable.just<T>(this)

fun <T> Single<T>.bindSchedulers(): Single<T> = this
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.bindSchedulers(): Flowable<T> = this
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T, R> biFunction(function: (T, R) -> T): BiFunction<T, R, T> = BiFunction(function)


fun findSessionsMismatches(repo: Repository) = repo.schedule()
    .flatMap { Flowable.fromIterable(it.timeslots) }
    .flatMap { Flowable.fromIterable(it.sessionIds) }
    .distinct()
    .toList()
    .zipWith(repo.sessions().map { it.id }.toList(), biFunction { idsInSchedule, idsInSessions ->
        "ids in sessions and not in schedule: ".logE()
        idsInSessions.subtract(idsInSchedule).logD()

        "ids in schedule and not in sessions: ".logE()
        idsInSchedule.subtract(idsInSessions).logD()

        listOf<Int>()
    })
    .toCompletable()

fun findAllTags(repo: Repository) = repo.sessions()
    .flatMap { Flowable.fromIterable(it.tags) }
    .distinct()
    .sorted()
    .toList()
    .doOnSuccess {
        "all available tags:".logE()
        it.logE()
    }
    .toCompletable()
