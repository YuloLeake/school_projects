# WRITTEN BY YULO LEAKE
#
# Fall 2015, CSCI 410 (PROGRAMMING LANGUAGE)
# FINAL LANGUAGE PROJECT - RUBY IMPLEMENTATION

require 'optparse'

# Simple Prefix Tree class to contain all dictionary words
class Trie
  def initialize
    @root = Node.new(' ')
  end

  # insert word into trie
  def insert(word)
    node = @root
    word.each_char do |c|
      # node is leaf, make it a node
      if node.children.nil?
        node.children = {}
      end

      # node has no children, populate it with new node with char c
      next_node = node.children[c]
      if next_node.nil?
        next_node = Node.new(c)
        node.children[c] = next_node
      end
      node = next_node
    end
    node.terminal = true
  end

  # return 1 if found word, 
  #        0 if word is found but not terminal (possible prefix), 
  #        -1 if not found (not a possible prefix)
  def search_word(word)
    depth = 0
    w_len = word.length
    
    node = @root
    word.each_char do |c|
      if node.children.nil?
        # word not found and is not a possible prefix
        return -1
      end
      next_node = node.children[c]
      if next_node.nil?
        # word not found and is not a possible prefix
        return -1
      end
      depth += 1
      node = next_node
    end
    if node.nil?
        # word not found and is not a possible prefix
      return -1
    end
    if node.terminal
      # exact word found!
      return 1
    else
      # exact word not found; may be a possible prefix for another word in trie
      return 0
    end
  end
end

# Node that contains character value and its children
class Node
  attr_accessor :value, :children, :terminal

  def initialize(value)
    @value = value
    @children = nil
    @terminal = false
  end
end

# Generator that returns a word found in the dictionary trie and the position where that word starts from
def gen_checkit(line, dict, rev)
  g = Enumerator.new do |g| # generator
    # enumerate all combination (2 char +) of substrings in given line
    for i in 0..line.length-1
      for j in i+1..line.length-1
        chunk = line[i..j]  # substring
        p = dict.search_word(chunk)  # check if chunk is actual word
        
        # substring is actual word in dict, yield it
        if p == 1
          # position depends on looking from left or right of the line
          pos = 
            if rev == 0
              (i + 1)
            else
              (line.length - i)
            end
          g.yield chunk, pos

        # substring is not a possible prefix of actual word, skip
        elsif p == -1
          break
        end
      end
    end
  end
end

def print_solution(word, r1, c1, r2, c2)
  print word, " ", r1, ",", c1, " ", r2, ",", c2,"\n"
end

# Main word-search logic
def word_search(puzzle, dict)
  h = puzzle.length
  w = puzzle[0].length

  # Do left to right and right to left
  for r in 0..(h-1)
    word = puzzle[r]

    words = gen_checkit(word, dict, 0)
    begin
      while true do
        pair = words.next
        wo =  pair[0]
        st = pair[1]
        en = st + wo.length - 1
        print_solution(wo, r+1, st, r+1, en)
      end
    rescue
    end
    words = gen_checkit(word.reverse, dict, 1)
    begin
      while true do
        pair = words.next
        wo =  pair[0]
        st = pair[1]
        en = st - wo.length + 1
        print_solution(wo, r+1, st, r+1, en)
      end
    rescue
    end
  end 

  # Puzzle is only a single line, return
  return if h == 1

  # Do top to bottom and bottom to top
  for c in 0..(w-1)
    word = ""
    for r in 0..(h-1)
      word << puzzle[r][c]
    end

    words = gen_checkit(word, dict, 0)
    begin
      while true do
        pair = words.next
        wo = pair[0]
        st = pair[1]
        en = st + wo.length - 1
        print_solution(wo, st, c+1, en, c+1)
      end
    rescue
    end

    words = gen_checkit(word.reverse!, dict, 1)
    begin
      while true do
        pair = words.next
        wo = pair[0]
        st = pair[1]
        en = st - wo.length + 1
        print_solution(wo, st, c+1, en, c+1)
      end
    rescue
    end
  end

  # Do top left to bottom right & top right to bottom left (rows)
  scan_num = (h - 1)
  for r in 0..(h-2)
    m = if scan_num > (w-1)
          (w-1)
        else
          scan_num
        end
    word1 = ""
    word2 = ""
    for c in 0..m
      word1 << puzzle[r+c][c]
      word2 << puzzle[r+c][w-c-1]
    end
    scan_num -= 1

    words = gen_checkit(word1, dict, 0)
    begin
      while true do
        pair = words.next
        wo = pair[0]
        st = pair[1]; en = wo.length - 1
        rs = r + st;  re = rs + en
        cs = st;      ce = st + en
        print_solution(wo, rs, cs, re, ce)
      end
    rescue
    end

    words = gen_checkit(word1.reverse!, dict, 1)
    begin
      while true do
        pair = words.next
        wo = pair[0]
        st = pair[1]; en = wo.length - 1
        rs = r + st;  re = rs - en
        cs = st;      ce = st - en
        print_solution(wo, rs, cs, re, ce)
      end
    rescue
    end

    words = gen_checkit(word2, dict, 0)
    begin
      while true do
        pair = words.next
        wo = pair[0]
        st = pair[1];    en = wo.length - 1
        rs = r + st;     re = rs + en
        cs = w - st + 1; ce = cs - en
        print_solution(wo, rs, cs, re, ce)
      end
    rescue
    end

    words = gen_checkit(word2.reverse!, dict, 1)
    begin
      while true do
        pair = words.next
        wo = pair[0]
        st = pair[1];    en = wo.length - 1
        rs = r + st;     re = rs - en
        cs = w - st + 1; ce = cs + en
        print_solution(wo, rs, cs, re, ce)
      end
    rescue
    end
  end

  # Do top left to bottom right & top right to bottom left (columns) 
  scan_num = (w - 2)
  for c in 1..(w-2)
    m = if scan_num > (h-1)
          (h-1)
        else
          scan_num
        end
    word1 = ""
    word2 = ""
    for r in 0..m
      word1 << puzzle[r][c+r]
      word2 << puzzle[r][(w-1)-c-r]
    end
    scan_num -= 1
    
    words = gen_checkit(word1, dict, 0)
    begin
      while true do
        pair = words.next
        wo = pair[0]
        st = pair[1]; en = wo.length - 1
        rs = st;      re = rs + en
        cs = c + st;  ce = cs + en
        print_solution(wo, rs, cs, re, ce)
      end
    rescue
    end

    words = gen_checkit(word1.reverse!, dict, 1)
    begin
      while true do
        pair = words.next
        wo = pair[0]
        st = pair[1]; en = wo.length - 1
        rs = st;      re = rs - en
        cs = c + st;  ce = cs - en
        print_solution(wo, rs, cs, re, ce)
      end
    rescue
    end

    words = gen_checkit(word2, dict, 0)
    begin
      while true do
        pair = words.next
        wo = pair[0]
        st = pair[1];  en = wo.length - 1
        rs = st;       re = rs + en
        cs = w-st-c+1; ce = cs - en
        print_solution(wo, rs, cs, re, ce)
      end
    rescue
    end

    words = gen_checkit(word2.reverse!, dict, 1)
    begin
      while true do
        pair = words.next
        wo = pair[0]
        st = pair[1];  en = wo.length - 1
        rs = st;       re = rs - en
        cs = w-st-c+1; ce = cs + en
        print_solution(wo, rs, cs, re, ce)
      end
    rescue
    end
  end
end

# main
# Read in options (-d and -f)
options = {:dict => nil, :puzz => nil}
OptionParser.new do |opts|
  opts.banner = "Usage: puzzle.rb [-d dictionary_fn] [-f puzzle_fn]"
  opts.on('-d d') do |d|
    options[:dict] = d
  end
  opts.on('-f f') do |f|
    options[:puzz] = f
  end
end.parse!

dict = options[:dict]
puzz = options[:puzz]

# Read and populate dictionary trie
trie = Trie.new
begin
  dict = "dict" if dict == nil
  File.open(dict).each_line do |line|
    trie.insert(line.rstrip!)
  end
rescue
  abort "Dictionary file not found"
end

# Read and create puzzle block
puzzle = []
if puzz != nil
  begin
    File.open(puzz).each_line do |line|
      puzzle << line.rstrip!   # append to list
    end
  rescue
    abort "Puzzle file not found"
  end
else
  # Stdin option to import puzzle
  STDIN.each_line do |line|
    puzzle << line.rstrip!
  end
end

word_search(puzzle, trie)