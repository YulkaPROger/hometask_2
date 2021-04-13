package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        return User.makeUser(fullName = fullName, email = email, password = password)
            .also {
                if (userIsRegister(it))
                    throw IllegalArgumentException("A user with this email already exists")
                else map[it.login] = it
            }
    }

    fun loginUser(login: String, password: String): String? {

        val loginUser = if (login.contains("@")) login else getCorrectPhone(login)

        return map[loginUser.trim()]?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        val loginUser = getCorrectPhone(rawPhone)
        return User.makeUser(fullName = fullName, phone = loginUser)
            .also {
                if (userIsRegister(it) || !phoneIsValid(rawPhone))
                    throw IllegalArgumentException("A user with this phone already exists")
                else map[it.login] = it
            }
    }

    private fun getCorrectPhone(phone: String):String{
        return phone.replace("[^+\\d]".toRegex(), "")
            .let {
                if(it.count() >= 11 && it.startsWith("+")) it else throw IllegalArgumentException("A user with this phone already exists")
            }
    }

    private fun phoneIsValid(phone: String): Boolean {
        return phone.replace("[^+\\d]".toRegex(), "")
            .let {
                it.count() == 12 && it.startsWith("+")
            }
    }

    private fun userIsRegister(user: User): Boolean {
        return map.containsKey(user.login)
    }

    fun requestAccessCode(login: String) {
        val loginUser = if (login.contains("@")) login else getCorrectPhone(login)
        map[loginUser]?.let {
            it.changePassword(it.accessCode!!, it.generateAccessCode())
        }
    }


}