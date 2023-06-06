(ns user
  (:require
    [io.aviso.repl :as repl]
    [net.lewisship.trace :as trace]))

(repl/install-pretty-exceptions)
(trace/setup-default)

