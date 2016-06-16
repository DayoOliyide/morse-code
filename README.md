# morse-code

A little Clojure library for transmitting morse code using
Arduino's onboard LED (default pin 13)

## Usage

Add to the dependencies vector in your project.clj

```clojure
[com.dayooliyide/morse-code "0.1.0"]
```

From your REPL
```clojure
(use 'morse-code.core)

  (init-board!) ;; Initialises a connection to the board and
                ;; returns a clojure map representation of the board.

  (blink! 100)  ;; switch LED on for 100 milliseconds
  (blink! 1000) ;; switch LED on for 1000 milliseconds

  (dot!)  ;; Flash LED for a dot
  (dash!) ;; Flash LED for a dash

  (morse! "SOS")
  (morse! "HELLO")

  ;; Morse Code Speed
  ;; There is no agreed universal duration for the dot signal.
  ;; The minimum morse speed to qualify for a Grade II license is 5 words per minute (5 wpm).
  ;; A word is made of 5 letters/characters and the words are interposed by space.
  ;; Thus the time taken to encode "MORSE WORDS MORSE WORDS 12345" should be
  ;; less than/equal to 60s.
  (time
   (morse! "MORSE WORDS MORSE WORDS 12345"))

```

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
