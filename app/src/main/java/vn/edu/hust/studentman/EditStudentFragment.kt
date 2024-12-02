package vn.edu.hust.studentman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class EditStudentFragment : Fragment() {
    private val args: EditStudentFragmentArgs by navArgs()
    private val studentViewModel: StudentViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val studentNameEditText = view.findViewById<EditText>(R.id.edit_student_name)
        val studentIdEditText = view.findViewById<EditText>(R.id.edit_student_id)
        val btnSaveChanges = view.findViewById<Button>(R.id.btn_save_changes)

        // Get current student data using Safe Args
        val studentId = args.studentId
        studentViewModel.getStudentById(studentId)?.let { student ->
            studentNameEditText.setText(student.studentName)
            studentIdEditText.setText(student.studentId)
        }

        btnSaveChanges.setOnClickListener {
            val name = studentNameEditText.text.toString()
            val id = studentIdEditText.text.toString()

            if (name.isNotEmpty() && id.isNotEmpty()) {
                studentViewModel.updateStudent(studentId, name, id)
                findNavController().navigateUp()
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}