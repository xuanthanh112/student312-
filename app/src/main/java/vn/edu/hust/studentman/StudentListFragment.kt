package vn.edu.hust.studentman

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle

class StudentListFragment : Fragment() {
    private val studentViewModel: StudentViewModel by activityViewModels()
    private lateinit var studentAdapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_student_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.list_view_students)
        studentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = studentAdapter

        registerForContextMenu(listView)

        // Observe students LiveData
        studentViewModel.students.observe(viewLifecycleOwner, Observer { students ->
            studentAdapter.clear()
            studentAdapter.addAll(students.map { "${it.studentName} - ${it.studentId}" })
            studentAdapter.notifyDataSetChanged()
        })

        // Use MenuHost API to handle menu creation and item selection
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_add_new -> {
                        findNavController().navigate(R.id.action_studentListFragment_to_addStudentFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val student = studentViewModel.students.value?.get(info.position)

        return when (item.itemId) {
            R.id.context_edit -> {
                student?.let {
                    val action = StudentListFragmentDirections
                        .actionStudentListFragmentToEditStudentFragment(it.studentName, it.studentId)
                    findNavController().navigate(action)
                }
                true
            }
            R.id.context_remove -> {
                student?.let { confirmDeleteStudent(it) }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun confirmDeleteStudent(student: StudentModel) {
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to delete this student?")
            .setPositiveButton("Yes") { _, _ -> studentViewModel.deleteStudent(student) }
            .setNegativeButton("No", null)
            .show()
    }
}