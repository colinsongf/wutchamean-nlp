(ns wutchamean.core-test
  (:require [midje.sweet :refer :all]
            [wutchamean.core :refer :all]))

(def grammar
  {:grammar {
             :animal ["dog" "cat" "hunted wumpus" "superb lyrebird of southern australia" "Australian Bear" "Austrian Cat Southern-style"]
             :person ["Bill" "Kris" "Trevor" "Cat Woman" "Dogwoman" "Bird Man" "hunter" "Australian" "Austrian" "Warren Beatty"]}})

(fact "strings get split"
      (split-words-in-phrase-list (-> grammar :grammar :animal)) 
      => [["dog" {:index 0, :phrase "dog"}]
          ["cat" {:index 0, :phrase "cat"}]
          ["hunted" {:index 0, :phrase "hunted wumpus"}]
          ["wumpus" {:index 1, :phrase "hunted wumpus"}]
          ["superb" {:index 0, :phrase "superb lyrebird of southern australia"}]
          ["lyrebird" {:index 1, :phrase "superb lyrebird of southern australia"}]
          ["of" {:index 2, :phrase "superb lyrebird of southern australia"}]
          ["southern" {:index 3, :phrase "superb lyrebird of southern australia"}]
          ["australia" {:index 4, :phrase "superb lyrebird of southern australia"}]
          ["australian" {:index 0, :phrase "Australian Bear"}]
          ["bear" {:index 1, :phrase "Australian Bear"}]
          ["austrian" {:index 0, :phrase "Austrian Cat Southern-style"}]
          ["cat" {:index 1, :phrase "Austrian Cat Southern-style"}]
          ["southern" {:index 2, :phrase "Austrian Cat Southern-style"}]
          ["style" {:index 3, :phrase "Austrian Cat Southern-style"}]])

(fact "process-grammar"
      (process-grammar (:grammar grammar))
      => {"australia" [["australia"
                        {:class :animal,
                         :index 4,
                         :phrase "superb lyrebird of southern australia"}]],
          "dog" [["dog" {:class :animal, :index 0, :phrase "dog"}]],
          "lyrebird" [["lyrebird"
                       {:class :animal,
                        :index 1,
                        :phrase "superb lyrebird of southern australia"}]],
          "of" [["of"
                 {:class :animal,
                  :index 2,
                  :phrase "superb lyrebird of southern australia"}]],
          "austrian" [["austrian" {:class :person, :index 0, :phrase "Austrian"}]
                      ["austrian"
                       {:class :animal, :index 0, :phrase "Austrian Cat Southern-style"}]],
          "bill" [["bill" {:class :person, :index 0, :phrase "Bill"}]],
          "bird" [["bird" {:class :person, :index 0, :phrase "Bird Man"}]],
          "hunted" [["hunted" {:class :animal, :index 0, :phrase "hunted wumpus"}]],
          "warren" [["warren" {:class :person, :index 0, :phrase "Warren Beatty"}]],
          "superb" [["superb"
                     {:class :animal,
                      :index 0,
                      :phrase "superb lyrebird of southern australia"}]],
          "style" [["style"
                    {:class :animal, :index 3, :phrase "Austrian Cat Southern-style"}]],
          "man" [["man" {:class :person, :index 1, :phrase "Bird Man"}]],
          "bear" [["bear" {:class :animal, :index 1, :phrase "Australian Bear"}]],
          "wumpus" [["wumpus" {:class :animal, :index 1, :phrase "hunted wumpus"}]],
          "dogwoman" [["dogwoman" {:class :person, :index 0, :phrase "Dogwoman"}]],
          "woman" [["woman" {:class :person, :index 1, :phrase "Cat Woman"}]],
          "hunter" [["hunter" {:class :person, :index 0, :phrase "hunter"}]],
          "kris" [["kris" {:class :person, :index 0, :phrase "Kris"}]],
          "australian" [["australian" {:class :person, :index 0, :phrase "Australian"}]
                        ["australian"
                         {:class :animal, :index 0, :phrase "Australian Bear"}]],
          "southern" [["southern"
                       {:class :animal,
                        :index 3,
                        :phrase "superb lyrebird of southern australia"}]
                      ["southern"
                       {:class :animal, :index 2, :phrase "Austrian Cat Southern-style"}]],
          "cat" [["cat" {:class :person, :index 0, :phrase "Cat Woman"}]
                 ["cat" {:class :animal, :index 0, :phrase "cat"}]
                 ["cat"
                  {:class :animal, :index 1, :phrase "Austrian Cat Southern-style"}]],
          "beatty" [["beatty" {:class :person, :index 1, :phrase "Warren Beatty"}]],
          "trevor" [["trevor" {:class :person, :index 0, :phrase "Trevor"}]]})

(fact "match token (hutne)"
      (match-token (process-grammar (:grammar grammar)) "hutne")
      => [{:confidence 0.6666666666666667,
           :match "hunted",
           :matches
           [{:class :animal, :index 0, :phrase "hunted wumpus"}]}
          {:confidence 0.6666666666666667,
           :match "hunter",
           :matches [{:class :person, :index 0, :phrase "hunter"}]}])

(fact "match token (cart)"
      (match-token (process-grammar (:grammar grammar)) "cart")
      => [{:confidence 0.6666666666666667,
           :match "cat",
           :matches
           [{:class :person, :index 0, :phrase "Cat Woman"}
            {:class :animal, :index 0, :phrase "cat"}
            {:class :animal, :index 1, :phrase "Austrian Cat Southern-style"}]}])

(fact "tokenize"
      (tokenize (process-grammar (:grammar grammar)) "Dog cat wumpus hutne")
      => {0
          {:matches
           [{:confidence 1.0,
             :match "dog",
             :matches [{:class :animal, :index 0, :phrase "dog"}]}],
           :original "Dog",
           :position 0},
          1
          {:matches
           [{:confidence 1.0,
             :match "cat",
             :matches
             [{:class :person, :index 0, :phrase "Cat Woman"}
              {:class :animal, :index 0, :phrase "cat"}
              {:class :animal,
               :index 1,
               :phrase "Austrian Cat Southern-style"}]}],
           :original "cat",
           :position 1},
          2
          {:matches
           [{:confidence 1.0,
             :match "wumpus",
             :matches [{:class :animal, :index 1, :phrase "hunted wumpus"}]}],
           :original "wumpus",
           :position 2},
          3
          {:matches
           [{:confidence 0.6666666666666667,
             :match "hunted",
             :matches [{:class :animal, :index 0, :phrase "hunted wumpus"}]}
            {:confidence 0.6666666666666667,
             :match "hunter",
             :matches [{:class :person, :index 0, :phrase "hunter"}]}],
           :original "hutne",
           :position 3}})