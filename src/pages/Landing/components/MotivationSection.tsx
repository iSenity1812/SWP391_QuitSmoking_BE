import { Button } from "@/components/ui/button"

export function MotivationSection() {
  return (
    <section className="py-16 py-16 bg-gradient-to-br from-emerald-200 via-emerald-100 to-sky-100 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900">
      <div className="container mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mx-auto max-w-3xl text-center">
          <h2 className="text-3xl text-slate-800 dark:text-white font-bold">
            "Mỗi bước nhỏ đều có ý nghĩa. Hành trình tự do của bạn bắt đầu từ hôm nay."
          </h2>
          <p className="mt-4 text-lg text-muted-foreground">
            Tham gia cùng hàng nghìn người khác đã thành công bỏ thuốc lá với ứng dụng của chúng tôi. Cuộc sống mới của
            bạn chỉ cách một cú nhấp chuột.
          </p>
          <div className="mt-8 flex flex-col gap-4 sm:flex-row sm:justify-center">
            <Button size="lg" className="bg-emerald-500 hover:bg-emerald-600 font-bold">
              Bắt Đầu Miễn Phí
            </Button>
            <Button size="lg" className="text-slate-900 dark:text-white font-bold" variant="outline">
              Tìm Hiểu Thêm
            </Button>
          </div>
        </div>
      </div>
    </section>
  )
}
