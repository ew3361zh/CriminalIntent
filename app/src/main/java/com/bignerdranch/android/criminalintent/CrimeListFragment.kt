package com.bignerdranch.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

//    Required interface for hosting activities
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private lateinit var crimeRecyclerView: RecyclerView
//    private var adapter: CrimeAdapter? = null
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    // called when a fragment is attached to an activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    // defines CrimeLIstFragment.OnCreate Bundle and lets Fragment Manager know CrimeListFragment needs to receive menu callbacks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // allows CrimeListFragment to use the fragment_crime_list layout view and find the RecylcerView in the layout file
    // when create recycler view, immediately give it a layoutmanager object for it to be able to work
    // recycler view does not position items on screen. that is delegated to layoutmanager which
    // also defines how scrolling works
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter

//        updateUI()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }

            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null // set to null because after you can't access activity or count on it continuing to exist
    }

    // when called, this passes menu resource to populate menu instance with items defined in the file
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    // responss to menuitem selection by creating a new crime
    // returns a boolean value to indicate no further processing needed, else it returns false
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // connect adapter to recycler view
//    private fun updateUI() {
//        val crimes = crimeListViewModel.crimes
    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    // implement a ViewHolder - recyclerView expects an item view to be wrapped in an instance of a ViewHolder
    // which stores a ref to an item's view
    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
    View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }
        // when given a Crime to bind, CrimeHolder will now update the title and date TextViews to reflect
        // the state of the Crime
        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility = if(crime.isSolved) {
                View.VISIBLE
                } else {
                    View.GONE
                }
            }

        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    // create second inner class for adapter which is a controller object that sites between the RecyclerView
    // and data set which RecyclerView should display
    // Adapter is responsible for creating necessary ViewHolders and binding ViewHolders to data from model layer
    private inner class CrimeAdapter(var crimes: List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {

        // responsible for creating a view to display and wrapping view in a view holder and returning the result
        // here, inflate list_item_view and pass resulting view to new instance of CrimeHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        // responsible for populating a fiven holder with crime from given position. use the data to set
        // the text in the corresponding text views
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
//            holder.apply {
//                titleTextView.text = crime.title
//                dateTextView.text = crime.date.toString()
//            }
            holder.bind(crime)
        }

        // returns number of items in the list of crimes for recyclerview to know how many items
        // are in data set backing it
        override fun getItemCount(): Int {
            return crimes.size // in book example, there is no return Int and it just says crimes.size
        }

    }

    // good to supply newInstance function that activities can call to get instance of a fragment
    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}