(ns morse-code.core
  (:use firmata.core))

(def ^:private LED-PIN 13)

;;when realised, open connection to board and set led-pin to output
(def ^:private BOARD (delay
                      (->  (open-serial-board :auto-detect)
                           (set-pin-mode LED-PIN :output))))

(def dot-duration 200)
(def dash-duration (* 3 dot-duration))

(defn signal-interval []
  (Thread/sleep dot-duration))

(defn char-interval []
  (Thread/sleep dash-duration))

(defn space-interval []
  (Thread/sleep (* 7 dot-duration)))

(defn blink!
  ([duration]
   (blink! @BOARD LED-PIN duration))

  ([board led-pin duration]
   (set-digital board led-pin :high)
   (Thread/sleep duration)
   (set-digital board led-pin :low)
   nil))

(defn dot!
  ([]
   (dot! @BOARD LED-PIN dot-duration))

  ([board]
   (dot! board LED-PIN dot-duration))

  ([board led-pin duration]
   (blink! board led-pin duration)))

(defn dash!
  ([]
   (dash! @BOARD LED-PIN dash-duration))

  ([board]
   (dash! board LED-PIN dash-duration))

  ([board led-pin duration]
   (blink! board led-pin duration)))


(def signal-map {\A [dot! dash!]
                 \B [dash! dot! dot! dot!]
                 \C [dash! dot! dash! dot!]
                 \D [dash! dot! dot!]
                 \E [dot!]
                 \F [dot! dot! dash! dot!]
                 \G [dash! dash! dot!]
                 \H [dot! dot! dot! dot!]
                 \I [dot! dot!]
                 \J [dot! dash! dash! dash!]
                 \K [dash! dot! dash!]
                 \L [dot! dash! dot! dot!]
                 \M [dash! dash!]
                 \N [dash! dot!]
                 \O [dash! dash! dash!]
                 \P [dot! dash! dash! dot!]
                 \Q [dash! dash! dot! dash!]
                 \R [dot! dash! dot!]
                 \S [dot! dot! dot!]
                 \T [dash!]
                 \U [dot! dot! dash!]
                 \V [dot! dot! dot! dash!]
                 \W [dot! dash! dash!]
                 \X [dash! dot! dot! dash!]
                 \Y [dash! dot! dash! dash!]
                 \Z [dash! dash! dot! dot!]
                 \1 [dot! dash! dash! dash! dash!]
                 \2 [dot! dot! dash! dash! dash!]
                 \3 [dot! dot! dot! dash! dash!]
                 \4 [dot! dot! dot! dot! dash!]
                 \5 [dot! dot! dot! dot! dot!]
                 \6 [dash! dot! dot! dot! dot!]
                 \7 [dash! dash! dot! dot! dot!]
                 \8 [dash! dash! dash! dot! dot!]
                 \9 [dash! dash! dash! dash! dot!]
                 \0 [dash! dash! dash! dash! dash!]})

(defn signal-char!
  ([char]
   (signal-char! @BOARD char))

  ([board char]
   (when-let [signals (get signal-map char)]
     (doseq [signal-call (interpose (fn [_] (signal-interval)) signals)]
       (signal-call board)))))

(defn signal-word!
  ([word]
   (signal-word! @BOARD word))

  ([board word]
   (let [char-call (fn [char] (fn [] (signal-char! board char)))
         calls (map char-call word)]
     (doseq [f (interpose char-interval calls)]
       (f)))))

(defn signal-word-seq!
  ([words-seq]
   (signal-word-seq! @BOARD words-seq))

  ([board words-seq]
   (let [words-seq (if (sequential? words-seq) words-seq [words-seq])
         word-call (fn [word] (fn [] (signal-word! board word)))
         calls (map word-call words-seq)]
     (doseq [f (interpose space-interval calls)]
       (f)))))

(defn morse!
  ([s]
   (morse! @BOARD s))

  ([board s]
   (assert  (re-matches #"[0-9a-zA-Z\ ]+" s) "Supporting only a subset of the Morse code i.e letters, numbers and space !")
   (let [ss (clojure.string/upper-case s)
         words (clojure.string/split ss #"\s+")]
     (signal-word-seq! board words))))




(comment

  (blink! 100)
  (blink! 1000)
  (dot!)
  (dash!)
  (do
    (dot!) (signal-interval) (dot!) (signal-interval) (dash!) (signal-interval) (dash!))

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


  ;(release-event-channel @BOARD ch)
  (close! @BOARD)

  )
