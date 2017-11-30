package cz.eman.android.devfest.addon.game.function.repository.model

/**
 * @author Pavel Habzansky (pavel.habzansky@eman.cz)
 *
 * Data class KeyPart serves as a model for representaion of private key part
 *
 * @property location String representing location where to get this KeyPart
 * @property controlWord String representing a clue for key sorting
 * @constructor Initializes KeyPart and its properties
 */
data class KeyPart(val controlWord: String, val location: String)