(ns user
  (:require
    [io.aviso.repl :as repl]
    [net.lewisship.trace :as trace]
    [nextjournal.clerk :as clerk]))

(repl/install-pretty-exceptions)
(trace/setup-default)

(clerk/serve! {:browse? true
               :port 9999
               :watch-paths ["src" "notebooks"]})

(comment
  (clerk/clear-cache!)
  (clerk/show! 'nextjournal.clerk.tap)
  )
