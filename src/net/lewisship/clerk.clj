(ns net.lewisship.clerk
  (:require [nextjournal.clerk :as clerk]))

(clerk/serve! {:browse?     true
               :port        9999
               :watch-paths ["src" "notebooks"]})

(comment
  (clerk/clear-cache!)
  (clerk/show! 'nextjournal.clerk.tap)
  )
