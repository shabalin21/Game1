package com.example.myapplication.domain.casino.engine

import com.example.myapplication.domain.casino.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class BlackjackEngine @Inject constructor() {

    private var deck = mutableListOf<Card>()

    init {
        reshuffle()
    }

    fun reshuffle() {
        deck.clear()
        for (suit in Suit.entries) {
            for (rank in Rank.entries) {
                deck.add(Card(suit, rank))
            }
        }
        deck.shuffle()
    }

    fun drawCard(): Card {
        if (deck.size < 10) {
            reshuffle()
        }
        return deck.removeAt(0)
    }

    fun calculateHandValue(hand: List<Card>): Int {
        var value = hand.sumOf { it.value }
        var aces = hand.count { it.rank == Rank.ACE }
        
        while (value > 21 && aces > 0) {
            value -= 10
            aces--
        }
        return value
    }

    fun isBlackjack(hand: List<Card>): Boolean {
        return hand.size == 2 && calculateHandValue(hand) == 21
    }

    fun isBust(hand: List<Card>): Boolean {
        return calculateHandValue(hand) > 21
    }

    fun shouldDealerHit(dealerHand: List<Card>): Boolean {
        return calculateHandValue(dealerHand) < 17
    }

    fun determineOutcome(playerHand: List<Card>, dealerHand: List<Card>): BlackjackOutcome {
        val playerValue = calculateHandValue(playerHand)
        val dealerValue = calculateHandValue(dealerHand)

        if (playerValue > 21) return BlackjackOutcome.BUST
        if (isBlackjack(playerHand) && !isBlackjack(dealerHand)) return BlackjackOutcome.BLACKJACK
        if (dealerValue > 21) return BlackjackOutcome.WIN
        if (playerValue > dealerValue) return BlackjackOutcome.WIN
        if (playerValue < dealerValue) return BlackjackOutcome.LOSS
        if (isBlackjack(dealerHand) && !isBlackjack(playerHand)) return BlackjackOutcome.LOSS
        
        return BlackjackOutcome.PUSH
    }

    fun calculatePayout(bet: Int, outcome: BlackjackOutcome): Int {
        return when (outcome) {
            BlackjackOutcome.BLACKJACK -> (bet * 2.5).toInt()
            BlackjackOutcome.WIN -> bet * 2
            BlackjackOutcome.PUSH -> bet
            BlackjackOutcome.LOSS, BlackjackOutcome.BUST -> 0
        }
    }
}
