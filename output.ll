; ModuleID = 'module'
source_filename = "module"

@x = global i32 3
@i = global i32 0
@j = global i32 3

define void @f(i32 %0, i32 %1) {
fEntry:
  %x = alloca i32, align 4
  store i32 %0, i32* %x, align 4
  %i = alloca i32, align 4
  store i32 %1, i32* %i, align 4
  %j = alloca i32, align 4
  %x1 = load i32, i32* %x, align 4
  store i32 %x1, i32* %j, align 4
  %x2 = load i32, i32* %x, align 4
  %j3 = load i32, i32* %j, align 4
  %subtmp = sub i32 %x2, %j3
  store i32 %subtmp, i32* %x, align 4
  ret void
}

define i32 @w() {
wEntry:
  ret i32 0
}

define i32 @main(i32 %0) {
mainEntry:
  %i = alloca i32, align 4
  store i32 %0, i32* %i, align 4
  %j = alloca i32, align 4
  %x = load i32, i32* @x, align 4
  store i32 %x, i32* %j, align 4
  %a = alloca i32, align 4
  %returnValue = call i32 @w()
  store i32 %returnValue, i32* %a, align 4
  %a1 = load i32, i32* %a, align 4
  ret i32 %a1
}
