package ithd.common.model

class Message {
    var s: String? = null
    var c: String? = null
    var ts: String? = null
    var fr: String? = null
    var to: String? = null
    var n: String? = null
    var m: String? = null
    var sdp: String? = null
    var ice: Ice? = null
    var info: String? = null

    constructor() {}
    constructor(
        s: String?,
        c: String?,
        ts: String?,
        fr: String?,
        to: String?,
        n: String?,
        m: String?,
        sdp: String?,
        ice: Ice?,
        info: String?
    ) {
        this.s = s
        this.c = c
        this.ts = ts
        this.fr = fr
        this.to = to
        this.n = n
        this.m = m
        this.sdp = sdp
        this.ice = ice
        this.info = info
    }

    override fun toString(): String {
        return "Message{" +
                "s='" + s + '\'' +
                ", c='" + c + '\'' +
                ", ts='" + ts + '\'' +
                ", fr='" + fr + '\'' +
                ", to='" + to + '\'' +
                ", n='" + n + '\'' +
                ", m='" + m + '\'' +
                ", sdp='" + sdp + '\'' +
                ", ice=" + ice +
                ", info='" + info + '\'' +
                '}'
    }

    class Ice {
        var candidate: String? = null
        var sdpMid: String? = null
        var sdpMLineIndex = 0

        constructor() {}
        constructor(candidate: String?, sdpMid: String?, sdpMLineIndex: Int) {
            this.candidate = candidate
            this.sdpMid = sdpMid
            this.sdpMLineIndex = sdpMLineIndex
        }

        override fun toString(): String {
            return "Ice{" +
                    "candidate='" + candidate + '\'' +
                    ", sdpMid='" + sdpMid + '\'' +
                    ", sdpMLineIndex='" + sdpMLineIndex + '\'' +
                    '}'
        }
    }
}