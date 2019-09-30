package com.example.gmails4

import android.app.Activity


class CommonModelClass {
    /** A private Constructor prevents any other class from instantiating.  */

    private var baseActivity: Activity? = null


    /**
     * used to clear CommonModelClass(SingletonClass) Memory
     */
    fun clear() {
        singletonObject = null
    }


    @Throws(CloneNotSupportedException::class)
    fun clone(): Any {
        throw CloneNotSupportedException()
    }

    //getters and setters starts from here.it is used to set and get a value

    fun getbaseActivity(): Activity? {
        return baseActivity
    }

    fun setbaseActivity(baseActivity: Activity) {
        this.baseActivity = baseActivity
    }

    companion object {
        private var singletonObject: CommonModelClass? = null
        @Synchronized
        fun getSingletonObject(): CommonModelClass {
            if (singletonObject == null) {
                singletonObject = CommonModelClass()
            }
            return singletonObject!!
        }
    }

   /* companion object Factory {
        var cars = mutableListOf<Int>()

        fun makeCar(horsepowers: Int): Int {
           // val car = Int(horsepowers)
            //cars.add(car)
            return car
        }
    }*/

}//   Optional Code
