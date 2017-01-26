package com.software.ssp.erkc.utils

import java.util.*

/**
 * @author Alexander Popov on 26/01/2017.
 */
abstract class NaturalOrderComparator<T : Any> : Comparator<T> {

    fun compareRight(a: String, b: String): Int {
        var bias = 0
        var ia = 0
        var ib = 0

        while (true) {
            val ca = charAt(a, ia)
            val cb = charAt(b, ib)

            if (!Character.isDigit(ca) && !Character.isDigit(cb)) {
                return bias
            } else if (!Character.isDigit(ca)) {
                return -1
            } else if (!Character.isDigit(cb)) {
                return +1
            } else if (ca < cb) {
                if (bias == 0) {
                    bias = -1
                }
            } else if (ca > cb) {
                if (bias == 0)
                    bias = +1
            } else if (ca.toInt() == 0 && cb.toInt() == 0) {
                return bias
            }
            ia++
            ib++
        }
    }

    override fun compare(left: T, right: T): Int {
        val a = getString(left)
        val b = getString(right)
        var ia = 0
        var ib = 0
        var nza = 0
        var nzb = 0
        var ca: Char
        var cb: Char
        var result: Int

        while (true) {
            nzb = 0
            nza = nzb

            ca = charAt(a, ia)
            cb = charAt(b, ib)

            while (Character.isSpaceChar(ca) || ca == '0') {
                if (ca == '0') {
                    nza++
                } else {
                    nza = 0
                }

                ca = charAt(a, ++ia)
            }

            while (Character.isSpaceChar(cb) || cb == '0') {
                if (cb == '0') {
                    nzb++
                } else {
                    nzb = 0
                }

                cb = charAt(b, ++ib)
            }

            if (Character.isDigit(ca) && Character.isDigit(cb)) {
                result = compareRight(a.substring(ia), b.substring(ib))
                if (result != 0) {
                    return result
                }
            }

            if (ca.toInt() == 0 && cb.toInt() == 0) {
                return nza - nzb
            }

            if (ca < cb) {
                return -1
            } else if (ca > cb) {
                return +1
            }

            ++ia
            ++ib
        }
    }

    protected open fun getString(p0: T): String {
        return p0.toString()
    }

    fun charAt(s: String, i: Int): Char {
        if (i >= s.length) {
            return 0.toChar()
        } else {
            return s[i]
        }
    }

}