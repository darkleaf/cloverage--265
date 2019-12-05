(ns coroutine
  (:require
   [cloroutine.core :refer [cr]]))

(defn ! [x]
  x)

(defn handle! []
  nil)

(defn example []
  (cr {! handle!}
      (let [x (! 42)]
        "some code")))
