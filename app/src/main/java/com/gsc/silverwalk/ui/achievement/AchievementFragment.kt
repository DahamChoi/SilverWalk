package com.gsc.silverwalk.ui.achievement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gsc.silverwalk.HistoryActivity
import com.gsc.silverwalk.R
import com.gsc.silverwalk.userinfo.UserInfo
import kotlinx.android.synthetic.main.cardview_history.view.*
import kotlinx.android.synthetic.main.fragment_achievement.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class AchievementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_achievement, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        achievement_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Change Tab
                setAchievementData(tab?.position!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        addHistory()
        setAchievementData(0)
    }

    fun setAchievementData(index: Int) {
        val calendar = Calendar.getInstance()
        // Today
        if (index == 0) {
            calendar.add(Calendar.DATE, -1)
        }
        // Weekly
        else if (index == 1) {
            calendar.add(Calendar.DATE, -7)
        }
        // Monthly
        else if (index == 2) {
            calendar.add(Calendar.MONTH, -1)
        }
        // Yearly
        else {
            calendar.add(Calendar.YEAR, -1)
        }
        val timestamp = Timestamp(Date(calendar.timeInMillis))

        Firebase.firestore
            .collection("users")
            .document(UserInfo.getInstance().uid)
            .collection("history")
            .whereGreaterThan("time", timestamp)
            .get()
            .addOnSuccessListener {
                if(isVisible) {
                    var totalSteps = 0L
                    var totalAveragePace = 0L
                    var totalBurnCalories = 0L
                    var totalHeartRate = 0L
                    var totalDistance = 0.0
                    var totalTime = 0L
                    var count = 0L

                    for (result in it) {
                        totalSteps += result.data.get("steps") as Long
                        totalAveragePace += result.data.get("average_pace") as Long
                        totalBurnCalories += result.data.get("calories") as Long
                        totalHeartRate += result.data.get("heart_rate") as Long
                        totalDistance += result.data.get("distance") as Double
                        totalTime += result.data.get("time_second") as Long
                        ++count
                    }

                    achievement_today_steps_text.setText(totalSteps.toString())
                    achievement_today_averagepace_text.setText((totalAveragePace / count).toString())
                    achievement_today_calories_text.setText(totalBurnCalories.toString())
                    achievement_today_heartrate_text.setText((totalHeartRate / count).toString())
                    achievement_today_distance_text.setText(
                        ((totalDistance * 10).roundToInt() / 10.0).toString()
                    )
                    achievement_today_time_text.setText(
                        String.format("%2d:%2d", totalTime / 60, totalTime % 60)
                    )
                }
            }
            .addOnFailureListener {
                Log.d("TAG", "Fail FireStore")
            }
    }

    fun addHistory() {
        Firebase.firestore
            .collection("users")
            .document(UserInfo.getInstance().uid)
            .collection("history")
            .get()
            .addOnSuccessListener {
                if(isVisible) {
                    for (result in it) {
                        val historyLayout = layoutInflater.inflate(
                            R.layout.cardview_history,
                            achievement_history_linearlayout, false
                        )

                        historyLayout.setOnClickListener {
                            val historyActivityIntent = Intent(context, HistoryActivity::class.java)
                            historyActivityIntent.putExtra(
                                "steps", result.data.get("steps") as Long
                            )
                            historyActivityIntent.putExtra(
                                "average_pace", result.data.get("average_pace") as Long
                            )
                            historyActivityIntent.putExtra(
                                "distance", result.data.get("distance") as Double
                            )
                            historyActivityIntent.putExtra(
                                "heart_rate", result.data.get("heart_rate") as Long
                            )
                            historyActivityIntent.putExtra(
                                "location", result.data.get("location").toString()
                            )
                            historyActivityIntent.putExtra(
                                "time_second", result.data.get("time_second") as Long
                            )
                            historyActivityIntent.putExtra(
                                "calories", result.data.get("calories") as Long
                            )

                            startActivity(historyActivityIntent)
                        }
                        historyLayout.history_card_view_text_1.setText(
                            result.data.get("location").toString()
                        )
                        val timestamp = result.data.get("time") as Timestamp
                        val timeDate = timestamp.toDate()
                        historyLayout.history_card_view_text_2.setText(
                            SimpleDateFormat("MMM dd, yyyy", Locale.US).format(timeDate)
                        )
                        historyLayout.history_card_view_text_3.setText(
                            SimpleDateFormat("h:mm a", Locale.US).format(timeDate)
                        )

                        achievement_history_linearlayout.addView(historyLayout)
                    }
                }
            }
            .addOnFailureListener {

            }
    }
}