package com.ahmedabdelmeged.firestore.util

import com.ahmedabdelmeged.firestore.model.Rating
import java.util.*

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 */
class RatingUtil {

    companion object {

        private val REVIEW_CONTENTS = arrayOf(
                // 0 - 1 stars
                "This was awful! Totally inedible.",

                // 1 - 2 stars
                "This was pretty bad, would not go back.",

                // 2 - 3 stars
                "I was fed, so that's something.",

                // 3 - 4 stars
                "This was a nice meal, I'd go back.",

                // 4 - 5 stars
                "This was fantastic!  Best ever!")

        /**
         * Get a list of random Rating POJOs.
         */
        fun getRandomList(length: Int): List<Rating> {
            val result = ArrayList<Rating>()

            for (i in 0 until length) {
                result.add(getRandom())
            }

            return result
        }

        /**
         * Get the average rating of a List.
         */
        fun getAverageRating(ratings: List<Rating>): Double {
            var sum = 0.0

            for (rating in ratings) {
                sum += rating.rating
            }

            return sum / ratings.size
        }

        /**
         * Create a random Rating POJO.
         */
        private fun getRandom(): Rating {
            val rating = Rating()
            val random = Random()

            val score = random.nextDouble() * 5.0
            val text = REVIEW_CONTENTS[Math.floor(score).toInt()]

            rating.userId = UUID.randomUUID().toString()
            rating.userName = "Random User"
            rating.rating = score
            rating.text = text
            return rating
        }
    }

}