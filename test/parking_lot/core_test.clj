(ns parking-lot.core-test
  (:require [clojure.test     :refer [deftest is]]
            [java-time        :refer [local-date-time]]
            [parking-lot.core :refer [total-price]]))

(deftest total-price-test
  (is (=  100 (total-price {:start-date-time  (local-date-time 2022 4 18  6  0  0)      ; 夜間割引は06:00までで06:00に入庫した、と言われそうなので、06 :00入庫は夜間割引で。
                            :finish-date-time (local-date-time 2022 4 18  7  0  0)})))
  (is (=  200 (total-price {:start-date-time  (local-date-time 2022 4 18  6  0  0)
                            :finish-date-time (local-date-time 2022 4 18  7  0  1)})))
  (is (=  200 (total-price {:start-date-time  (local-date-time 2022 4 18  6  0  0)
                            :finish-date-time (local-date-time 2022 4 18  7 20  0)})))
  (is (=  300 (total-price {:start-date-time  (local-date-time 2022 4 18  6  0  0)
                            :finish-date-time (local-date-time 2022 4 18  7 20  1)})))
  (is (= 1100 (total-price {:start-date-time  (local-date-time 2022 4 18  0  0  0)
                            :finish-date-time (local-date-time 2022 4 18  8 20  0)})))
  (is (= 1100 (total-price {:start-date-time  (local-date-time 2022 4 18  0  0  0)      ; 24Hで最大1100円。
                            :finish-date-time (local-date-time 2022 4 18  8 20  1)})))
  (is (= 1100 (total-price {:start-date-time  (local-date-time 2022 4 18  0  0  0)      ; 24Hで最大1100円。
                            :finish-date-time (local-date-time 2022 4 19  0  0  0)})))
  (is (= 1200 (total-price {:start-date-time  (local-date-time 2022 4 18  0  0  0)
                            :finish-date-time (local-date-time 2022 4 19  1  0  0)})))
  (is (= 2200 (total-price {:start-date-time  (local-date-time 2022 4 18  0  0  0)
                            :finish-date-time (local-date-time 2022 4 19  8 20  0)})))
  (is (= 2300 (total-price {:start-date-time  (local-date-time 2022 4 18  0  0  0)      ; 24H最大1100円は1回だけ。
                            :finish-date-time (local-date-time 2022 4 19  8 20  1)}))))
