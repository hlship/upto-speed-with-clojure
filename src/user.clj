(ns user
  (:require
    [clj-commons.pretty.repl :as repl]
    [net.lewisship.trace :as trace]))

(repl/install-pretty-exceptions)
(trace/setup-default)


