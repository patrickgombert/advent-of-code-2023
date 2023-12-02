def calibrate(input)
  input.map { |line| _line_digit(line) }.sum
end

def _line_digit(line)
  digits = line.strip.split('').filter do |char|
    char.match?(/[[:digit:]]/)
  end
  (digits[0] + digits[-1]).to_i
end

puts calibrate(File.readlines('input'))
