package cz.eman.android.devfest.addon.game.function.repository.model

/**
 * @author Pavel Habzansky (pavel.habzansky@eman.cz)
 *
 * Data class User serves as a model for player information
 *
 * @param cdhScore Player's ingame score
 * @param email Player's email
 * @param name Player's name
 * @param photoUrl Player's photoURL
 * @param uid Player's uid
 * @constructor Initializes User and all his properties
 */
data class User(val cdhScore: Long,
                val email: String,
                val name: String,
                val photoUrl: String,
                val uid: String)