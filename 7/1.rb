class Hand
  ORDERING = ['A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2']

  include Comparable
  attr_reader :cards

  def initialize(input)
    @cards, @bid = input.strip.split(" ")
    @cards = @cards.split('')
    @bid = @bid.to_i
  end

  def <=>(other)
    comparison = type <=> other.type
    if comparison.zero?
      @cards.each_with_index do |card, i|
        this_card = _ordering(card)
        other_card = _ordering(other.cards[i])
        card_comparison = other_card <=> this_card
        if !card_comparison.zero?
          return card_comparison
        end
      end
    end
    comparison
  end

  def type
    counts = @cards.group_by(&:itself)
      .values
      .map(&:count)
      .sort
      .reverse

    if counts[0] == 5
      7
    elsif counts[0] == 4
      6
    elsif counts[0] == 3 && counts[1] == 2
      5
    elsif counts[0] == 3
      4
    elsif counts[0] == 2 && counts[1] == 2
      3
    elsif counts[0] == 2
      2
    else
      1
    end
  end

  def winnings(rank)
    @bid * rank
  end

  def _ordering(card)
    ORDERING.find_index(card)
  end
end

hands = File.readlines('input').map do |line|
  Hand.new(line)
end.sort

sum = 0
hands.each_with_index do |hand, rank|
  sum += hand.winnings(rank + 1)
end
puts sum
