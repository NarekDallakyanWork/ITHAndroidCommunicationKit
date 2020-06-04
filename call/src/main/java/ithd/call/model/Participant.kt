package ithd.call.model

class Participant {
    var participant: String? = null

    constructor() {}
    constructor(participant: String?) {
        this.participant = participant
    }

    override fun toString(): String {
        return "VTParticipant{" +
                "participant='" + participant + '\'' +
                '}'
    }
}