(ns net.lewisship.clerk)

(clerk/serve! {:browse? true
               :port 9999
               :watch-paths ["src" "notebooks"]})

(comment
  (clerk/clear-cache!)
  (clerk/show! 'nextjournal.clerk.tap)
  )
