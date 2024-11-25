package vn.edu.hust.studentman

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
  private val students = mutableListOf(
    StudentModel("Nguyễn Văn An", "SV001"),
    StudentModel("Trần Thị Bảo", "SV002"),
    StudentModel("Lê Hoàng Cường", "SV003"),
    StudentModel("Phạm Thị Dung", "SV004"),
    StudentModel("Đỗ Minh Đức", "SV005"),
    StudentModel("Vũ Thị Hoa", "SV006"),
    StudentModel("Hoàng Văn Hải", "SV007"),
    StudentModel("Bùi Thị Hạnh", "SV008"),
    StudentModel("Đinh Văn Hùng", "SV009"),
    StudentModel("Nguyễn Thị Linh", "SV010"),
    StudentModel("Phạm Văn Long", "SV011"),
    StudentModel("Trần Thị Mai", "SV012"),
    StudentModel("Lê Thị Ngọc", "SV013"),
    StudentModel("Vũ Văn Nam", "SV014"),
    StudentModel("Hoàng Thị Phương", "SV015"),
    StudentModel("Đỗ Văn Quân", "SV016"),
    StudentModel("Nguyễn Thị Thu", "SV017"),
    StudentModel("Trần Văn Tài", "SV018"),
    StudentModel("Phạm Thị Tuyết", "SV019"),
    StudentModel("Lê Văn Vũ", "SV020")
  )

  private lateinit var studentAdapter: ArrayAdapter<String>
  private lateinit var addStudentLauncher: ActivityResultLauncher<Intent>
  private lateinit var editStudentLauncher: ActivityResultLauncher<Intent>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)

    val listView = findViewById<ListView>(R.id.list_view_students)
    studentAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, students.map { "${it.studentName} - ${it.studentId}" })
    listView.adapter = studentAdapter

    // Đăng ký context menu cho ListView
    registerForContextMenu(listView)

    listView.setOnItemClickListener { _, _, position, _ ->
      val selectedStudent = students[position]
      Toast.makeText(this, "Clicked on ${selectedStudent.studentName}", Toast.LENGTH_SHORT).show()
    }

    // Register the activity result launchers
    addStudentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == RESULT_OK) {
        val studentName = result.data?.getStringExtra("STUDENT_NAME") ?: ""
        val studentId = result.data?.getStringExtra("STUDENT_ID") ?: ""
        students.add(StudentModel(studentName, studentId))
        updateStudentAdapter()
      }
    }

    editStudentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == RESULT_OK) {
        val updatedStudentId = result.data?.getStringExtra("STUDENT_ID")
        val updatedStudentName = result.data?.getStringExtra("STUDENT_NAME")

        val student = students.find { it.studentId == updatedStudentId }
        student?.studentName = (updatedStudentName ?: student?.studentName).toString()
        updateStudentAdapter()
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)  // Inflate menu layout
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_add_new -> {
        val intent = Intent(this, AddStudentActivity::class.java)
        addStudentLauncher.launch(intent)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
    super.onCreateContextMenu(menu, v, menuInfo)
    menuInflater.inflate(R.menu.context_menu, menu)
  }

  override fun onContextItemSelected(item: MenuItem): Boolean {
    val selectedPosition = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position
    val selectedStudent = students[selectedPosition]

    return when (item.itemId) {
      R.id.context_edit -> {
        val intent = Intent(this, EditStudentActivity::class.java)
        intent.putExtra("STUDENT_ID", selectedStudent.studentId)
        intent.putParcelableArrayListExtra("STUDENT_LIST", ArrayList(students))
        editStudentLauncher.launch(intent)
        true
      }
      R.id.context_remove -> {
        confirmDeleteStudent(selectedStudent)
        true
      }
      else -> super.onContextItemSelected(item)
    }
  }

  private fun confirmDeleteStudent(student: StudentModel) {
    AlertDialog.Builder(this)
      .setMessage("Are you sure you want to delete this student?")
      .setPositiveButton("Yes") { _, _ -> deleteStudent(student) }
      .setNegativeButton("No", null)
      .show()
  }

  private fun deleteStudent(student: StudentModel) {
    students.remove(student)
    updateStudentAdapter()
    Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show()
  }

  private fun updateStudentAdapter() {
    studentAdapter.clear()
    studentAdapter.addAll(students.map { "${it.studentName} - ${it.studentId}" })
    studentAdapter.notifyDataSetChanged()
  }
}