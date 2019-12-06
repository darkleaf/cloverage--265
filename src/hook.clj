(ns hook
  (:require
   [cloverage.instrument :refer :all :as i]))

(alter-var-root #'list-type
                (fn [_]
                  (fn [head]

                    (condp #(%1 %2) (#'i/maybe-resolve-symbol head)
                      ;; namespace-less specials
                      '#{try}         :try     ; try has to special case catch/finally
                      '#{if do throw} :do      ; these special forms can recurse on all args
                      '#{let* loop*}  :let
                      '#{def}         :def     ; def can recurse on initialization expr
                      '#{fn*}         :fn
                      ;;'#{set!}        :set     ; set must not evaluate the target expr
                      '#{.}           :dotjava
                      '#{case*}       :case*
                      '#{new}         :new
                      '#{reify*}      :reify*
                      ;; FIXME: monitor-enter monitor-exit
                      ;; FIXME: import*?

                      ;; namespaced macros
                      `#{cond}        :cond    ; special case cond to avoid false partial
                      `#{loop let}    :let
                      `#{letfn}       :letfn
                      `#{for doseq}   :for
                      `#{fn}          :fn
                      `#{defn}        :defn    ; don't expand defn to preserve stack traces
                      `#{defmulti}    :defmulti ; special case defmulti to avoid boilerplate
                      `#{defprotocol} :atomic   ; no code in protocols
                      `#{defrecord}   :record
                      `#{deftype*}    :deftype*
                      `#{ns}          :atomic


                      `#{letfn*} :letfn

                      ;; http://dev.clojure.org/jira/browse/CLJ-1330 means AOT-compiled definlines
                      ;; are broken when used indirectly. Work around - do not wrap the definline
                      `#{booleans bytes chars shorts floats ints doubles longs} :inlined
                      atomic-special?   :atomic
                      ;; XXX: we used to not do anything with unknown specials, now we wrap them
                      ;; in a macro, then macroexpand back to original form. Methinks it's ok.
                      special-symbol?   :unknown
                      (constantly true) :list))))
