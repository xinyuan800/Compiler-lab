; ModuleID = 'module'
source_filename = "module"

define i32 @f(i32 %0) {
fEntry:
  %i = alloca i32, align 4
  store i32 %0, i32* %i, align 4
  %i1 = load i32, i32* %i, align 4
  ret i32 %i1
}

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 1, i32* %a, align 4
  %b = alloca i32, align 4
  store i32 0, i32* %b, align 4
  %a1 = load i32, i32* %a, align 4
  %returnValue = call i32 @f(i32 %a1)
  store i32 %returnValue, i32* %b, align 4
  %a2 = load i32, i32* %a, align 4
  %returnValue3 = call i32 @f(i32 %a2)
  ret i32 %returnValue3
}
