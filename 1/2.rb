require_relative '1'

NUMERIC_WORDS = {
  'one'       => 'o1e',
  'two'       => 't2o',
  'three'     => 't3e',
  'four'      => 'f4r',
  'five'      => 'f5e',
  'six'       => 's6x',
  'seven'     => 's7n',
  'eight'     => 'e8t',
  'nine'      => 'n9e',
}

def digit_replacement(input)
  input.map do |line|
    NUMERIC_WORDS.keys.reduce(line) do |replacement, key|
      replacement.gsub(key, NUMERIC_WORDS[key])
    end
  end
end

puts calibrate(digit_replacement(File.readlines('input')))
