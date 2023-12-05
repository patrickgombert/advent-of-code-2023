require_relative '1'

class CardQueue
  def initialize(cards)
    @cards = cards
  end

  def process
    cards_to_process = Queue.new
    @cards.values.each { |c| cards_to_process << c }
    counter = 0
    while !cards_to_process.empty?
      card = cards_to_process.pop
      counter += 1
      (card.number + 1..card.number + card.count).each do |n|
        cards_to_process << @cards[n]
      end
    end
    counter
  end
end

cards = File.readlines('input').reduce({}) do |acc, line|
  card = Card.new(line)
  card.parse!
  acc[card.number] = card
  acc
end

card_queue = CardQueue.new(cards)
puts card_queue.process
