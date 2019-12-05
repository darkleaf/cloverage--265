(ns coroutine-test
  (:require [coroutine :as sut]
            [clojure.test :as t]))

(t/deftest coroutine
  (t/is (= 42 ((sut/example)))))
