(ns teamring0c13.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [clojure.java.shell :as shell])
  (:gen-class))


(defn -main
  [session-token]

  (let [base-url "http://ringzer0team.com/challenges/121/"


        response1 (client/get base-url
                              {:cookies {"PHPSESSID" {:value session-token}}})

        shellcode (-> (:body response1)
                      html/html-snippet
                      (html/select [:.message])
                      first
                      :content
                      (nth 2)
                      (subs 3))

        shellcode (-> shellcode
                      ; Remove the useless pushq
                      (clojure.string/replace #"\\x68.{16}\\x48\\x31" "\\\\x48\\\\x31")
                      ; Set %rdi to 1 instead of 0 so it prints in stdout instead of stdin and pad with nop.
                      ; mov    %rax,%rdi
                      ; nop
                      ; nop
                      (clojure.string/replace #"\\x0b\\x0f\\x05" "\\\\x0b\\\\x48\\\\x89\\\\xc7\\\\x90\\\\x90\\\\x0f\\\\x05"))

        _ (with-open [osc (io/output-stream "target/shellcode.h")]
            (.write osc (-> "const char SC[]=\"" .getBytes))
            (.write osc (-> shellcode .trim .getBytes))
            (.write osc (-> "\";\n" .getBytes)))

        _ (shell/sh "cc" "host.c" "-g" "-o" "target/solution")

        message (:out (shell/sh "target/solution"))

        _ (println message)

        response2 (client/get (str base-url message)
                              {:cookies {"PHPSESSID" {:value session-token}}})

        flag (-> (:body response2)
                 html/html-snippet
                 (html/select [:.alert-info])
                 first
                 :content
                 first)]

    (println flag))

  (System/exit 0))

