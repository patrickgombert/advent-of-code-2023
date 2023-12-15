def h(sequence)
  sequence.split('').reduce(0) do |acc, char|
    acc = acc + char.ord
    acc = acc * 17
    acc % 256
  end
end

def read(file_path)
  File.read(file_path).strip.gsub("\n", '').split(',')
end

puts read('input')
  .map { |sequence| h(sequence) }
  .sum
