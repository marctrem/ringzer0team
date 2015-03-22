(ns chal32.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]
            [pandect.algo.sha512 :as p512])
  (:gen-class))

(defn readeval-part [parts idx]
  (-> parts
      (nth idx)
      read-string
      eval))

(defn -main
  [session-token]

  (let [base-url "http://ringzer0team.com/challenges/32/"
        response1 (client/get base-url
                              {:cookies {"PHPSESSID" {:value session-token}}})

        message (-> (:body response1)
                    html/html-snippet
                    (html/select [:.message])
                    first
                    :content
                    (nth 2)
                    (subs 3))

        parts (clojure.string/split message #" ")

        ; Build expression in a non-abmiguous form: an AST.
        answ ((readeval-part parts 3)
               ((readeval-part parts 1)
                 (readeval-part parts 0)
                 (readeval-part parts 2))

               (Integer/parseInt (nth parts 4) 2))

        response2 (client/get (str base-url answ)
                              {:cookies {"PHPSESSID" {:value session-token}}})

        flag (-> (:body response2)
                 html/html-snippet
                 (html/select [:.alert-info])
                 first
                 :content
                 first)]

    (println flag)))

