; ModuleID = 'module'
source_filename = "module"

define void @f() {
fEntry:
  ret void
}

define i32 @main() {
mainEntry:
  call void @f()
  ret i32 0
}
