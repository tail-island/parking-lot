(ns parking-lot.core
  (:require [java-time :refer [after? before? hours local-time minutes plus]]))

(defn unit-price
  [duration price {:keys [start-date-time total-price] :as state}]
  (-> state
      (assoc :start-date-time (plus start-date-time duration))
      (assoc :total-price     (+ total-price price))))

(defn unit-price-once
  [id duration price state]
  (cond->> state
    ((complement contains?) state id) (#(-> (unit-price duration price %)
                                            (assoc id true)))))

(defn conditional-unit-price
  [condition duration price {:keys [start-date-time] :as state}]
  (cond->> state
    (condition (assoc state :finish-date-time (plus start-date-time duration))) (unit-price duration price)))

(defn night-discount?
  [{:keys [start-date-time finish-date-time]}]
  (let [night-start-time  (local-time 22 0)
        night-finish-time (local-time  6 0)]
    (->> [start-date-time finish-date-time]
         (map  #(.toLocalTime %))
         (some #(or (after?  % night-start-time)
                    (=       % night-start-time)
                    (before? % night-finish-time)
                    (=       % night-finish-time))))))

(defn actions
  [_state]
  ;; 利用者に不利となる条件を扱う場合は、stateに合わせて使用可能なactionを返すようにしてください。
  [(partial unit-price                             (minutes 20)  100)
   (partial conditional-unit-price night-discount? (minutes 60)  100)
   (partial unit-price-once :first-24-hour         (hours   24) 1100)])

(defn next-state
  [state action]
  (action state))

(defn goal?
  [{:keys [start-date-time finish-date-time]}]
  (or (after? start-date-time finish-date-time)
      (=      start-date-time finish-date-time)))

(defn total-price
  [state]
  ;; TODO: ダイクストラ法に変更する。
  (->> (loop [stack [(assoc state :total-price 0)] visited-states #{} total-prices []]
         (if ((complement empty?) stack)
           (let [state (peek stack)
                 stack (pop  stack)]
             (if (contains? visited-states state)
               (recur stack visited-states total-prices)
               (let [visited-states (conj visited-states state)]
                 (if (goal? state)
                   (recur stack visited-states (conj total-prices (:total-price state)))
                   (recur (vec (concat stack (map (partial next-state state) (actions state))))
                          visited-states
                          total-prices)))))
           total-prices))
       (apply min)))
