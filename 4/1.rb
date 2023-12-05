require 'set'

class Card
  attr_reader :number

  def initialize(line)
    @line = line
    @winning_numbers = Set[]
    @numbers = Set[]
  end

  def parse!
    card_number, numbers = @line.split(':')
    @number = card_number.split(' ')[1].to_i
    @winning_numbers, @numbers = numbers.split("|")
      .map(&:strip)
      .map { |n| n.split(' ') }
      .map do |ns|
        ns.map(&:to_i).reduce(Set[]) { |acc, n| acc << n; acc }
      end
  end

  def score
    if count.size.zero?
      0
    else
      2 ** (count.size - 1)
    end
  end

  def count
    @winning_numbers.intersection(@numbers).size
  end
end

puts(File.readlines('input').map do |line|
  card = Card.new(line)
  card.parse!
  card.score
end.sum)
