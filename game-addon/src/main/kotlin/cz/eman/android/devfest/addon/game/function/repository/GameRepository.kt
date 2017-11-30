package cz.eman.android.devfest.addon.game.function.repository

import com.google.firebase.database.*
import cz.eman.android.devfest.addon.game.function.repository.model.KeyPart

/**
 * Created by PavelHabzansky on 16.08.17.
 */
class GameRepository {

    var output: MutableList<KeyPart> = mutableListOf()
    var dbRef: DatabaseReference

    constructor() {
        this.dbRef = FirebaseDatabase.getInstance().reference
    }

    //TODO fun cdhProgress() = database.child("cdhProgress")

    fun setDBResource(url: String){
        this.dbRef = dbRef.root.child(url)
    }
}