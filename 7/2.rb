require_relative '1'

class JokerHand < Hand
  ORDERING = ['A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J']

  def type
    counts = @cards.group_by(&:itself)
    jokers = (counts.delete('J') || []).count
    # card_counts[0] won't exist below, so return early for convenience
    if jokers == 5
      return 7
    end

    card_counts = counts.values
      .map(&:count)
      .sort
      .reverse

    if card_counts[0] + jokers == 5
      7
    elsif card_counts[0] + jokers == 4
      6
    elsif card_counts[0] + jokers == 3 && card_counts.size == 2
      5
    elsif card_counts[0] + jokers == 3
      4
    elsif card_counts[0] + jokers == 2 && card_counts.size == 3
      3
    elsif card_counts[0] + jokers == 2 && card_counts.size == 4
      2
    else
      1
    end
  end

  def _ordering(card)
    ORDERING.find_index(card)
  end
end

hands = File.readlines('input').map do |line|
  JokerHand.new(line)
end.sort

sum = 0
hands.each_with_index do |hand, rank|
  sum += hand.winnings(rank + 1)
end
puts sum
